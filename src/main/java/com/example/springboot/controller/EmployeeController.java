package com.example.springboot.controller;

import com.example.springboot.model.Company;
import com.example.springboot.model.Employee;
import com.example.springboot.repository.CompanyRepository;
import com.example.springboot.repository.EmployeeRepository;
import com.example.springboot.repository.EmployeeDao;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    EmployeeDao employeeDao;

    @RequestMapping(value = "/employee/list") // homepage redirects here
    public String getAllEmployee(
            @RequestParam(value = "company", required = false, defaultValue = "0") Long
                    comId,
            @RequestParam(value = "gender", required = false, defaultValue = "0") int
                    gender,
            @RequestParam(value = "sort", required = false, defaultValue = "0") int
                    sortMode,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {
        final int pageSize = 1;
        model.addAttribute("comId", comId);
        model.addAttribute("gender", gender);
        model.addAttribute("sortMode", sortMode);
        Page<Employee> employees = employeeDao.filterAndSortEmployees(
                comId, gender, sortMode,
                PageRequest.of(page, pageSize)
        );
        model.addAttribute("page", page);
        model.addAttribute("pages", employees.getTotalPages());
        model.addAttribute("employees", employees.get());
        model.addAttribute("companies", companyRepository.findAll());
        return "employeeList";
    }

    @RequestMapping(value = "/employee/{id}")
    public String getEmployeeById(@PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeRepository.getById(id);
        model.addAttribute("employee", employee);
        return "employeeDetail";
    }

    @RequestMapping(value = "/employee/update/{id}")
    public String updateEmployee(
            @PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeRepository.getById(id);
        model.addAttribute(employee);
        model.addAttribute("companies", companyRepository.findAll());
        return "employeeUpdate";
    }

    @RequestMapping(value = "/employee/save")
    public String saveUpdate(@Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            return "employeeUpdate";
        } else {
            employeeRepository.save(employee);
            return "redirect:/employee/update/" + employee.getId();
        }
    }

    @RequestMapping(value = "/employee/add")
    public String addEmployee(Model model) {
        Employee employee = new Employee();
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("employee", employee);
        return "employeeAdd";
    }

    @RequestMapping(value = "/employee/insert")
    public String insertEmployee(
            Model model, @Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("employee", employee);
            return "employeeAdd";
        } else {
            employeeRepository.save(employee);
            return "redirect:/employee/" + employee.getId();
        }
    }

    @RequestMapping(value = "/employee/delete/{id}")
    public String deleteEmployee(@PathVariable(value = "id") Long id) {
        if (employeeRepository.findById(id).isPresent()) {
            Employee employee = employeeRepository.findById(id).get();
            employeeRepository.delete(employee);
        }
        return "redirect:/employee/list";
    }
}