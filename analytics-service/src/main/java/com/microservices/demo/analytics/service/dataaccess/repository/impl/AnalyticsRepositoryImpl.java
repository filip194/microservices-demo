package com.microservices.demo.analytics.service.dataaccess.repository.impl;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.microservices.demo.analytics.service.dataaccess.entity.BaseEntity;
import com.microservices.demo.analytics.service.dataaccess.repository.AnalyticsCustomRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class AnalyticsRepositoryImpl<T extends BaseEntity<PK>, PK> implements AnalyticsCustomRepository<T, PK>
{
    // injecting current Entity Manager to use in our custom methods for our custom batching logic,
    // where actually this entity manager is thread local reference to EntityManager object from container
    // NOTE: this is container managed bean, so that means we don't have deal with transaction management and/or
    // closing this manager
    // All is managed with @Transactional annotation (javax.transaction) handled by Spring
    @PersistenceContext
    protected EntityManager em;
    // we need this em to manually flush data to DB per batch size(50). Note that data only flushes to DB when
    // committed.
    // that means at the end of transaction, when commit is called, data will be sent to database in batches.
    //

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    protected int batchSize;

    @Override
    @Transactional
    public <S extends T> PK persist(S entity)
    {
        em.persist(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public <S extends T> void batchPersist(Collection<S> entities)
    {
        if (entities.isEmpty())
        {
            log.info("No entities found to insert!");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities)
        {
            log.trace("Persisting entity with ID {}", entity.getId());
            em.persist(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0)
            {
                em.flush();
                em.clear(); // we clear the first level cache so that we don't get any trouble because RAM is full
            }
        }
        // check if any entity is left un-flushed after the loop
        if (batchCnt % batchSize != 0)
        {
            em.flush();
            em.clear();
        }
    }

    @Override
    @Transactional
    public <S extends T> S merge(S entity)
    {
        return em.merge(entity);
    }

    @Override
    @Transactional
    public <S extends T> void batchMerge(Collection<S> entities)
    {
        if (entities.isEmpty())
        {
            log.info("No entities found to merge!");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities)
        {
            log.trace("Merging entity with ID {}", entity.getId());
            em.merge(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0)
            {
                em.flush();
                em.clear(); // we clear the first level cache so that we don't get any trouble because RAM is full
            }
        }
        // check if any entity is left un-flushed after the loop
        if (batchCnt % batchSize != 0)
        {
            em.flush();
            em.clear();
        }
    }

    @Override
    public void clear()
    {
        em.clear();
    }
}

