package fr.eni.javaee.repository;

import fr.eni.javaee.exception.BusinessException;
import fr.eni.javaee.model.Repas;

import java.time.LocalDate;
import java.util.List;

public interface RepasDao {

    void add(Repas repas) throws BusinessException;
    List<Repas> getAll() throws BusinessException;
    List<Repas> getRepasByDate(LocalDate date) throws BusinessException;
}

