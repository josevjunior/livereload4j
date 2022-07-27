package io.github.josevjunior.livereload4j.test;

import io.github.josevjunior.livereload4j.Application;
import static io.github.josevjunior.livereload4j.Application.launch;

public class MainV2 implements Application {

    @Override
    public void start(String[] args) throws Exception {
        System.out.println("Vai 9");        
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

}
