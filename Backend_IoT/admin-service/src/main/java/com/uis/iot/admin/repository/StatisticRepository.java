package com.uis.iot.admin.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uis.iot.common.model.StatusChangeDTO;

/**
 * Statistic Data access layer. Not using Spring Repositories as the queries are
 * far more complicated.
 * 
 * @author Federico
 *
 */
@Repository
public class StatisticRepository {

	@Autowired
	private EntityManager em;

	/**
	 * Retrieves the changes (10 latest) generated in the Job Service (using the
	 * notifications stored).
	 * 
	 * @param userId id of the user.
	 * @return a {@link List} of {@link StatusChangeDTO}.
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<StatusChangeDTO> getChanges(long userId) {
		/* @formatter:off */
		final StringBuilder queryBuilder = new StringBuilder(300)
			.append("SELECT ")
			.append("total.sent_date sentDate, ")
			.append("IFNULL(alive.alive_count, 0) alive, ")
			.append("(total.total_count - IFNULL(alive.alive_count, 0)) death ")
			.append("FROM ( ")
			.append("SELECT sent_date, COUNT(*) total_count ")
			.append("FROM notification ")
			.append("WHERE id_user = :userId ")
			.append("GROUP BY sent_date) total ")
			.append("LEFT JOIN (")
			.append("SELECT sent_date, alive, COUNT(*) alive_count ")
			.append("FROM notification ")
			.append("WHERE id_user = :userId AND alive = 1 ")
			.append("GROUP BY sent_date ")
			.append(") alive ")
			.append("ON alive.sent_date = total.sent_date ")
			.append("ORDER BY total.sent_date DESC ")
			.append("LIMIT 10");
		/* @formatter:on */

		// Use Hibernate to retrieve the results and map them to a StatusChangeDTO the
		// code is more complicated because it's not an entity but it's to avoid iterate
		// over the result.
		final Session session = em.unwrap(Session.class);
		return (List<StatusChangeDTO>) session.createNativeQuery(queryBuilder.toString())
				.addScalar("sentDate", TimestampType.INSTANCE).addScalar("alive", LongType.INSTANCE)
				.addScalar("death", LongType.INSTANCE).setParameter("userId", userId)
				.setResultTransformer(Transformers.aliasToBean(StatusChangeDTO.class)).getResultList();
	}

}
