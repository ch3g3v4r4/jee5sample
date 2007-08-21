package com.example.sample.model.business;

import javax.ejb.Local;

import com.example.sample.model.entities.Project;

@Local
public interface Sample {
    String sayHello(String name);
    Project createProject(Project p);
}
