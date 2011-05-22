package sample.core;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class DropInsBuilder {

	@PostConstruct
	private void init() {
		System.out.println("Hello");

	}
}
