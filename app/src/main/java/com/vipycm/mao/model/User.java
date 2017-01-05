package com.vipycm.mao.model;

/**
 * Created by mao on 2016/5/5.
 */
public class User {

    private String id;
    private String name;
    private String sex;
    private int age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id:").append(id);
        sb.append(" name:").append(name);
        sb.append(" sex:").append(sex);
        sb.append(" age:").append(age);
        return sb.toString();
    }
}
