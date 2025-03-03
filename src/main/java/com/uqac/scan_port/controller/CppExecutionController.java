package com.uqac.scan_port.controller;

import com.uqac.scan_port.service.CppExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CppExecutionController {

    @Autowired
    private CppExecutionService cppExecutionService;

    /**
     * Executes the C++ program with the specified IP address as a parameter.
     * @param ip the IP address to scan.
     * @return the output of the C++ program execution.
     */
    @GetMapping("/execute-cpp")
    public String executeCpp(@RequestParam String ip) {
        return cppExecutionService.executeCppProgram(ip);
    }
}