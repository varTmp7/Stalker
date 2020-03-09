package com.vartmp7.stalker.GsonBeans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Luogo {
    private long id;

    private String name;
    private long num_max_people;
    private ArrayList<Coordinata> coordinates;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Luogo && coordinates.size()== ((Luogo) obj).coordinates.size()){
            Luogo l = (Luogo) obj;

            for (int i = 0; i < coordinates.size(); i++) {
                if (coordinates.get(i)!= l.coordinates.get(i))
                    return false;
            }
            return l.getId()==getId() && getName().equals(l.getName()) && num_max_people == l.getNum_max_people();
        }
        return false;
    }

    public Coordinata getCentro() {
        Retta r1 = new Retta(coordinates.get(0), coordinates.get(2));
        return r1.intersezione(new Retta(coordinates.get(1), coordinates.get(3)));
    }


    public boolean isInPlace(Coordinata c){
        if (getCentro().getDistanceTo(c)< getRadius())
            return true;
        return false;
    }


    /**
     *  metodo usato per il POC per ottenere il raggio del tracciamento
     * @return
     */
    public double getRadius(){
        return (coordinates.get(0).getDistanceTo(coordinates.get(2))/2 + coordinates.get(0).getDistanceTo(coordinates.get(1))/2)/2;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Coordinata c : coordinates) {
            builder.append(c.toString());
        }

        return "\nid: " + getId() +
                "\nNome: " + getName() +
                "\nNum. Max Persone " + getNum_max_people() +
                "\nCoordinate: " + builder.toString();
    }


    public long getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Long.parseLong(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNum_max_people() {
        return num_max_people;
    }

    public void setNum_max_people(String num_max_people) {
        this.num_max_people = Long.parseLong(num_max_people);
    }

    public ArrayList<Coordinata> getCoordinate() {
        return coordinates;
    }

    public Luogo setCoordinate(ArrayList<Coordinata> coordinate) {
        this.coordinates = coordinate;
        return this;
    }

}
