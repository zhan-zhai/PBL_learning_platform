package com.SuperNova.model;

import javax.persistence.*;

@Table(name = "student_grade")
public class StudentGrade {

    @Id
    @Column(name = "item_id")
    private Integer item_id;

    @Id
    @Column(name = "p_id")
    private Integer p_id;

    @Id
    @Column(name = "u_id")
    private String u_id;

    private Double grade;

    /**
     * @return item_id
     */
    public Integer getitem_id() {
        return item_id;
    }

    public Integer getItem_id() {
        return item_id;
    }

    /**
     * @param item_id
     */
    public void setitem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    /**
     * @return p_id
     */
    public Integer getp_id() {
        return p_id;
    }

    public Integer getP_id() {
        return p_id;
    }

    /**
     * @param p_id
     */
    public void setp_id(Integer p_id) {
        this.p_id = p_id;
    }

    public void setP_id(Integer p_id) {
        this.p_id = p_id;
    }

    /**
     * @return u_id
     */
    public String gets_id() {
        return u_id;
    }

    public String getU_id() {
        return u_id;
    }

    /**
     * @param s_id
     */
    public void sets_id(String s_id) {
        this.u_id = s_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    /**
     * @return grade
     */
    public Double getGrade() {
        return grade;
    }

    /**
     * @param grade
     */
    public void setGrade(Double grade) {
        this.grade = grade;
    }
}