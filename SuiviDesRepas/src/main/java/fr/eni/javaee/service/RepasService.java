package fr.eni.javaee.service;

import fr.eni.javaee.exception.BusinessException;
import fr.eni.javaee.model.Aliment;
import fr.eni.javaee.model.Repas;
import fr.eni.javaee.repository.DaoFactory;
import fr.eni.javaee.repository.RepasDao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RepasService {

    private RepasDao repasDao;

    public RepasService() {
        this.repasDao = DaoFactory.getRepasDao();
    }

    public Repas addRepas(LocalDate jour, LocalTime heure, List<String> listeALiments) throws BusinessException {

        BusinessException be = new BusinessException();
        this.validateJourHeure(jour, heure, be);
        this.validateListeAliments(listeALiments, be);

        Repas repas = null;

        if(!be.hasError()) {
            repas = new Repas();
            repas.setJour(jour);
            repas.setHeure(heure);
            for(String aliment : listeALiments){
                repas.getListeAliments().add(new Aliment(aliment));
            }
            this.repasDao.add(repas);
        }else {
            throw be;
        }
        return repas;
    }

    public List<Repas> getAllRepas() throws BusinessException {
        return repasDao.getAll();
    }

    public List<Repas> getRepasByDate(LocalDate date) throws BusinessException {
        return repasDao.getRepasByDate(date);
    }

    private void validateJourHeure(LocalDate jour, LocalTime heure, BusinessException be) throws BusinessException {
        if(jour == null || jour.isAfter(LocalDate.now()) || (jour.isEqual(LocalDate.now()) && heure.isAfter(LocalTime.now()))) {
            be.addError(CodeExceptionService.ERROR_DATE);
            throw be;
        }
    }

    private void validateListeAliments(List<String> listeALiments, BusinessException be) throws BusinessException {
        if(listeALiments == null || listeALiments.isEmpty()) {
            be.addError(CodeExceptionService.ERROR_LISTE_ALIMENTS);
            throw be;
        }else {
            for(String aliment : listeALiments) {
                if(aliment.length() > 50) {
                    be.addError(CodeExceptionService.ERROR_ALIMENT);
                    throw be;
                }
            }
        }
    }
}

