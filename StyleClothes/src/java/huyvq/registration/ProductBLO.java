/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huyvq.registration;

import huyvq.drawl.HashMD5;
import huyvq.drawl.XMLUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Dell
 */
public class ProductBLO {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("StyleClothesPU");
    private List<Product> products;

    public ProductBLO() {
        products = new ArrayList<>();
    }

    public ProductBLO(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void persist(Object object) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(object);
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public Product addColor(String color, Product dto) {
        if (color.isEmpty()) {
            color = "undefined";
        }
        EntityManager em = emf.createEntityManager();
        Color cl = (Color) em.createQuery("SELECT r FROM Color r WHERE r.vietnamName LIKE :color1 OR r.englishName LIKE :color2")
                .setParameter("color1", color)
                .setParameter("color2", color)
                .getSingleResult();
        dto.setColorId(cl);
        System.out.println("add color success");
        return dto;
    }

    public int insertAllProducts(String typeId) {
        EntityManager em = emf.createEntityManager();
        int count = 0;
        int update = 0;
        for (Product product : products) {
            try {
                Product c = (Product) em.createQuery("SELECT r FROM Product r WHERE r.picture= :picture").setParameter("picture", product.getPicture()).getSingleResult();
                c.setColorId(product.getColorId());
                c.setLink(product.getLink());
                c.setName(product.getName());
                c.setPrice(product.getPrice());
            } catch (Exception e) {
                Category cate = em.find(Category.class, typeId);
                product.setTypeId(cate);
                if (product.getColorId() == null) {
                    addColor("indefined", product);
                }
                em.getTransaction().begin();
                em.persist(product);
                em.getTransaction().commit();
                count++;
            }
        }
        System.out.println("\ncào được " + products.size() + " sản phẩm\n");
        System.out.println("có " + count + " sản phẩm mới\n");
        System.out.println("===============================================================================================");
        return count;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
