package ru.yasuchenin.turtilla.DAO;

import java.util.List;

import org.hibernate.Session;

import ru.yasuchenin.turtilla.SignInfo;
import ru.yasuchenin.turtilla.HibernateUtil;

;

public class SignInfoDAOimpl implements SignInfoDAO {

	public void addSign(SignInfo signInfo) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.save(signInfo);
		session.getTransaction().commit();
	}

	public List<SignInfo> listSigns() {
		List<SignInfo> res;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		res = session.createCriteria(SignInfo.class).list();
		return res;
	}

	public void updateSign(SignInfo signInfo) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		session.update(signInfo);
		session.getTransaction().commit();
	}

}
