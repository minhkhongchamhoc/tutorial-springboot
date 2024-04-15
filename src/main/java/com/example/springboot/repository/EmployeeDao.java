package com.example.springboot.repository;

import com.example.springboot.model.Company;
import com.example.springboot.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class EmployeeDao {
    @Autowired
    EntityManager em;
    @Autowired
    CompanyRepository companyRepository;
    // data access methods go here
    public Page<Employee> filterAndSortEmployees(Long comId, int gender, int sortMode, Pageable pageable) {
        HibernateCriteriaBuilder cb =
                em.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class);
        // determining sort column
        Path<Object> sortColumn = employee.get("id"); // default sort column
        if (sortMode == 2 || sortMode == 3) {
            sortColumn = employee.get("name");
        }
        // determining sort order
        Order sortOrder = cb.desc(sortColumn); // default sort order
        if (sortMode == 1 || sortMode == 2) {
            sortOrder = cb.asc(sortColumn);
        }
        // filter criteria
        List<Predicate> predicates = new ArrayList<>();
        // filtering by company
        Optional<Company> comp = companyRepository.findById(comId);
        if (comp.isPresent()) {
            predicates.add(cb.equal(employee.get("company"), comp.get()));
        }
        // filtering by gender (0 means not filtered by gender)
        if (gender == 1) {
            predicates.add(cb.equal(employee.get("male"), false));
        } else if (gender == 2) {
            predicates.add(cb.equal(employee.get("male"), true));
        }
        // add a list of predicates as query criteria
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }
        // sort
        cq.orderBy(sortOrder);
        TypedQuery<Employee> tq = em.createQuery(cq);
        tq.setFirstResult((int) pageable.getOffset());
        tq.setMaxResults(pageable.getPageSize());
        return PageableExecutionUtils.getPage(
                tq.getResultList(),
                pageable,
                () -> em.createQuery(cq.createCountQuery())
                        .getSingleResult() // counting
        );

    }
}
