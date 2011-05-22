package sample.core;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class DropInsBuilder {

	@PostConstruct
	private void init() {
		System.out.println("Hello");

	}

    public String generateScript() {
        // TODO Auto-generated method stub
        return "System.out.println(1111)";
    }
}
