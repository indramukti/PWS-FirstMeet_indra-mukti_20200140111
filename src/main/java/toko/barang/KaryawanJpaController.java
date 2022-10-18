/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko.barang;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import toko.barang.exceptions.NonexistentEntityException;
import toko.barang.exceptions.PreexistingEntityException;

/**
 *
 * @author DELL
 */
public class KaryawanJpaController implements Serializable {

    public KaryawanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("toko_barang_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public KaryawanJpaController() {
    }
    
    

    public void create(Karyawan karyawan) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(karyawan);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findKaryawan(karyawan.getIdKaryawan()) != null) {
                throw new PreexistingEntityException("Karyawan " + karyawan + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Karyawan karyawan) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            karyawan = em.merge(karyawan);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = karyawan.getIdKaryawan();
                if (findKaryawan(id) == null) {
                    throw new NonexistentEntityException("The karyawan with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Karyawan karyawan;
            try {
                karyawan = em.getReference(Karyawan.class, id);
                karyawan.getIdKaryawan();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The karyawan with id " + id + " no longer exists.", enfe);
            }
            em.remove(karyawan);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Karyawan> findKaryawanEntities() {
        return findKaryawanEntities(true, -1, -1);
    }

    public List<Karyawan> findKaryawanEntities(int maxResults, int firstResult) {
        return findKaryawanEntities(false, maxResults, firstResult);
    }

    private List<Karyawan> findKaryawanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Karyawan.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Karyawan findKaryawan(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Karyawan.class, id);
        } finally {
            em.close();
        }
    }

    public int getKaryawanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Karyawan> rt = cq.from(Karyawan.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
