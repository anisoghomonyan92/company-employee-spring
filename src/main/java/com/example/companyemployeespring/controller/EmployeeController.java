package com.example.companyemployeespring.controller;
import com.example.companyemployeespring.entity.Company;
import com.example.companyemployeespring.entity.Employee;
import com.example.companyemployeespring.repository.CompanyRepository;
import com.example.companyemployeespring.repository.EmployeeRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Value("${task.management.images.folder}")
    private String folderPath;

    @GetMapping(value = "/employees")
    public String showEmployees(ModelMap modelMap) {
        List<Employee> employeeList = employeeRepository.findAll();
        List<Company> companyList = companyRepository.findAll();
        modelMap.addAttribute("employees", employeeList);
        modelMap.addAttribute("companies", companyList);
        return "employees";
    }

    @GetMapping(value = "/employee/add")
    public String addEmployeePage(ModelMap modelMap) {
        List<Company> all = companyRepository.findAll();
        modelMap.addAttribute("companies", all);
        return "addEmployee";
    }

    @PostMapping(value = "/employee/add")
    public String addEmployee(@ModelAttribute Employee employee,
                              @RequestParam("employeeImage") MultipartFile file) throws IOException {
        if (!file.isEmpty() && file.getSize() > 0) {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File newFile = new File(folderPath + File.separator + filename);
            file.transferTo(newFile);
            employee.setProfilePic(filename);
        }
        Company company = companyRepository.getReferenceById(employee.getCompany().getId());
        company.setSize(company.getSize() + 1);
        companyRepository.save(company);
        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping(value = "/employees/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("fileName") String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(folderPath + File.separator + fileName);
        return IOUtils.toByteArray(inputStream);

    }

    @GetMapping(value = "/employee/delete")
    public String delete(@RequestParam("id") int id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}
