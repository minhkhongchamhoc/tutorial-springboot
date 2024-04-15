package com.example.springboot.controller;

import com.example.springboot.model.Company;
import com.example.springboot.repository.CompanyRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CompanyController {
    @Autowired
    CompanyRepository companyRepository;

    @RequestMapping(value = "/company/list")
    public String getAllCompany(Model model) {
        List<Company> companies = companyRepository.findAll();
        model.addAttribute("companies", companies);
        return "companyList";
    }

    @RequestMapping(value = "/company/{id}")
    public String getCompanyById(@PathVariable(value = "id") Long id, Model model) {
        Company company = companyRepository.getById(id);
        model.addAttribute("company", company);
        return "companyDetail";
    }

    @RequestMapping(value = "/company/update/{id}")
    public String updateCompany(
            @PathVariable(value = "id") Long id, Model model) {
        Company company = companyRepository.getById(id);
        model.addAttribute(company);
        return "companyUpdate";
    }

    @RequestMapping(value = "/company/save")
    public String saveUpdate(@Valid Company company, BindingResult result) {
        if (result.hasErrors()) {
            return "companyUpdate";
        }
        companyRepository.save(company);
        return "redirect:/company/update/" + company.getId();
    }

    @RequestMapping(value = "/company/add")
    public String addCompany(Model model) {
        Company company = new Company();
        model.addAttribute("company", company);
        return "companyAdd";
    }

    @RequestMapping(value = "/company/insert")
    public String insertCompany(Company company) {
        companyRepository.save(company);
        return "redirect:/company/" + company.getId();
    }

    @RequestMapping(value = "/company/delete/{id}")
    public String deleteCompany(@PathVariable(value = "id") Long id) {
        if (companyRepository.findById(id).isPresent()) {
            Company company = companyRepository.findById(id).get();
            companyRepository.delete(company);
        }
        return "redirect:/company/list";
    }
}
