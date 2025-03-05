package com.example.GreetingApp.service;

import com.example.GreetingApp.model.Greeting;
import com.example.GreetingApp.model.AuthUser;

public interface IGreetingService {
    Greeting addGreeting(AuthUser user);
    Greeting getGreetingById(long id);
}