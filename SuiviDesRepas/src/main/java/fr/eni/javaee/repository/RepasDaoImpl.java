package fr.eni.javaee.repository;

import fr.eni.javaee.exception.BusinessException;
import fr.eni.javaee.model.Aliment;
import fr.eni.javaee.model.Repas;
import fr.eni.javaee.provider.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepasDaoImpl implements RepasDao {

    private static final String ADD_REPAS = "INSERT INTO Repas(date_repas, heure_repas) VALUE(?,?)";
    private static final String ADD_ALIMENT = "INSERT INTO Aliments(nom, id_repas) VALUES(?,?)";
    private static final String GET_ALL = "SELECT r.id as id_repas," +
            "r.date_repas AS jour," +
            "r.heure_repas AS heure," +
            "a.id AS id_aliment" +
            "a.nom" +
            "FROM Repas r" +
            "INNER JOIN Aliments a ON r.id = a.id_repas" +
            "ORDER BY r.date_repas desc, r.heure_repas desc";
    private static final String GET_BY_DATE = "SELECT r.id as i_repas," +
            "r.date_repas AS jour," +
            "r.heure_repas AS heure," +
            "a.id AS id_aliment" +
            "a.nom" +
            "FROM Repas r" +
            "INNER JOIN Aliments a ON r.id = a.id_repas" +
            "WHERE r.date_repas = ?" +
            "ORDER BY r.date_repas desc, r.heure_reas desc";

    @Override
    public void add(Repas repas) throws BusinessException {
        if(repas == null) {
            BusinessException be = new BusinessException();
            be.addError(CodeExceptionDao.INSERT_OBJECT_NULL);
            throw be;
        }

        try(Connection con = ConnectionProvider.getConnection()) {
            try {
                con.setAutoCommit(false);
                PreparedStatement pstmt = con.prepareStatement(ADD_REPAS, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setDate(1, java.sql.Date.valueOf(repas.getJour()));
                pstmt.setTime(2, java.sql.Time.valueOf(repas.getHeure()));
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if(rs.next()) {
                    repas.setId(rs.getInt(1));
                }
                rs.close();
                pstmt.close();
                pstmt = con.prepareStatement(ADD_ALIMENT, PreparedStatement.RETURN_GENERATED_KEYS);
                for(Aliment aliment : repas.getListeAliments()) {
                    pstmt.setString(1, aliment.getNom());
                    pstmt.setInt(2, repas.getId());
                    pstmt.executeUpdate();
                    rs = pstmt.getGeneratedKeys();
                    if(rs.next()) {
                        aliment.setId(rs.getInt(1));
                    }
                    rs.close();
                }
                pstmt.close();
                con.commit();
            }catch (Exception e) {
                e.printStackTrace();
                con.rollback();
                throw e;
            }
        }catch (Exception e) {
            e.printStackTrace();
            BusinessException be = new BusinessException();
            be.addError(CodeExceptionDao.INSERT_OBJECT_ECHEC);
            throw be;
        }
    }

    @Override
    public List<Repas> getAll() throws BusinessException {
        List<Repas> listeRepas = new ArrayList<>();
        try(Connection con = ConnectionProvider.getConnection()) {

            PreparedStatement pstmt = con.prepareStatement(GET_ALL);
            ResultSet rs = pstmt.executeQuery();
            Repas repas = new Repas();
            while(rs.next()) {
                if(rs.getInt("id")!= repas.getId()) {
                    repas = repasBuider(rs);
                    listeRepas.add(repas);
                }
                Aliment aliment = alimentBuilder(rs);
                repas.getListeAliments().add(aliment);
            }
        }catch(Exception e) {
            e.printStackTrace();
            BusinessException be = new BusinessException();
            be.addError(CodeExceptionDao.LECTURE_REPAS_ECHEC);
            throw be;
        }
        return listeRepas;
    }

    @Override
    public List<Repas> getRepasByDate(LocalDate date) throws BusinessException {
        List<Repas> listeRepas = new ArrayList<>();
        try(Connection con = ConnectionProvider.getConnection()) {

            PreparedStatement pstmt = con.prepareStatement(GET_BY_DATE);
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            Repas repas = new Repas();
            while(rs.next()) {
                if(rs.getInt("id")!=repas.getId()) {
                    repas = repasBuider(rs);
                    listeRepas.add(repas);
                }
                Aliment aliment = alimentBuilder(rs);
                repas.getListeAliments().add(aliment);
            }
        }catch(Exception e) {
            e.printStackTrace();
            BusinessException be = new BusinessException();
            be.addError(CodeExceptionDao.LECTURE_REPAS_ECHEC);
            throw be;
        }
        return listeRepas;
    }

    private Aliment alimentBuilder(ResultSet rs) throws SQLException {

        return new Aliment(rs.getInt("id"), rs.getString("nom"));

    }

    private Repas repasBuider(ResultSet rs) throws SQLException {
        Repas repas;
        repas = new Repas();
        repas.setId(rs.getInt("id"));
        repas.setJour(rs.getDate("date_repas").toLocalDate());
        repas.setHeure(rs.getTime("heure_repas").toLocalTime());
        return repas;
    }
}

