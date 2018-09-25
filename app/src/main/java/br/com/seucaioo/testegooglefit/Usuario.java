package br.com.seucaioo.testegooglefit;

import android.support.v7.app.AppCompatActivity;

public class Usuario {

    private String id;
    private String email;
    private String nome;
    private String heart;
    private String step;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public Usuario setId(String id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Usuario setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getNome() {
        return nome;
    }

    public Usuario setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public String getHeart() {
        return heart;
    }

    public Usuario setHeart(String heart) {
        this.heart = heart;
        return this;
    }

    public String getStep() {
        return step;
    }

    public Usuario setStep(String step) {
        this.step = step;
        return this;
    }
}
