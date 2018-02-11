package daggerok.facebook.repository;

import lombok.extern.slf4j.Slf4j;

import javax.ejb.TransactionAttribute;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static javax.ejb.TransactionAttributeType.NEVER;
import static javax.ejb.TransactionAttributeType.REQUIRED;

@Slf4j
@Singleton
@TransactionAttribute(NEVER)
public class PostQueryModelRepository {

  @PersistenceContext EntityManager em;

  @TransactionAttribute(REQUIRED)
  public void save(final PostQueryModel person) {
    em.persist(person);
  }

  public PostQueryModel findOne(final String postId) {
    return em.find(PostQueryModel.class, postId);
  }

  public List<PostQueryModel> findAll() {
    return em.createQuery("select pqm from PostQueryModel pqm", PostQueryModel.class)
             .getResultList();
  }

  public PostQueryModel findFirstLike(final String postId) {
    return em.createQuery("select pqm from PostQueryModel pqm where lower(pqm.postId) like lower(concat('%', :postId,'%'))", PostQueryModel.class)
             .setParameter("postId", postId)
             .getResultList()
             .stream()
             .findFirst()
             .orElse(null);
  }

  public List<PostQueryModel> findAllLike(final String postId) {
    return em.createQuery("select pqm from PostQueryModel pqm where lower(pqm.postId) like lower(concat('%', :postId,'%'))", PostQueryModel.class)
             .setParameter("postId", postId)
             .getResultList();
  }

  @TransactionAttribute(REQUIRED)
  public int deleteAllLike(final String postId) {
    return em.createQuery("delete from PostQueryModel pqm where lower(pqm.postId) like lower(concat('%', :postId,'%'))")
             .setParameter("postId", postId)
             .executeUpdate();
  }
}
