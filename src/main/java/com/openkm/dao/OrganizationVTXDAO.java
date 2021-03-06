package com.openkm.dao;

import com.openkm.bean.Document;
import com.openkm.bean.OrganizationVTXBean;
import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Organization;
import com.openkm.dao.bean.OrganizationVTX;
import com.openkm.dao.bean.User;
import com.openkm.dao.bean.UserOrganizationVTX;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.hibernate.*;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationVTXDAO {
	private static Logger log = LoggerFactory.getLogger(OrganizationVTXDAO.class);
	private static OrganizationVTXDAO single = new OrganizationVTXDAO();

	public OrganizationVTXDAO() {
	}

	public static OrganizationVTXDAO getInstance() {
		return single;
	}

	public List<Organization> getAllOrg() throws DatabaseException {
		log.debug("getAllOrg({})");
		String qs = "from OrganizationVTX o";

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);

			List<Organization> ret = q.list();
			log.debug("getAllOrg: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public Response createOrg(OrganizationVTXBean newOrg) throws DatabaseException {
		log.debug("createOrg({})");

		if (getOrganizationbyCode(newOrg.getOrgCode()) != null) {
			return Response.status(Response.Status.CONFLICT).entity("Mã đơn vị đã tồn tại").build();
		} else {


			Session session = null;
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				OrganizationVTX organizationVTX = new OrganizationVTX();
				organizationVTX.setCode(newOrg.getOrgCode());
				organizationVTX.setName(newOrg.getOrgName());
				organizationVTX.setParent(newOrg.getOrgParent());
				organizationVTX.setPath("");

				session.beginTransaction();
				Long id = (Long) session.save(organizationVTX);
				session.getTransaction().commit();

				if (newOrg.getOrgParent() == -1L) {
					organizationVTX.setPath('/' + String.valueOf(id) + '/');
				} else {
					organizationVTX.setPath(getOrganizationbyId(newOrg.getOrgParent()).getPath() + id + '/');
				}

				session.beginTransaction();
				session.save(organizationVTX);
				session.getTransaction().commit();
			} catch (HibernateException e) {
				throw new DatabaseException(e.getMessage(), e);
			} finally {
				HibernateUtil.close(session);
			}
		}
		return null;
	}

	public OrganizationVTX getOrganizationbyId(Long id) throws DatabaseException {
		String qs = "from OrganizationVTX o where o.id= :id";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("id", id);
			OrganizationVTX ret = (OrganizationVTX) q.setMaxResults(1).uniqueResult();
			log.debug("getOrg: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public OrganizationVTX getOrganizationbyCode(String code) throws DatabaseException {
		String qs = "from OrganizationVTX o where o.code= :code";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("code", code);
			OrganizationVTX ret = (OrganizationVTX) q.setMaxResults(1).uniqueResult();
			log.debug("getOrg: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public List<OrganizationVTX> search(String name, String code) throws DatabaseException {
		String qs = "from OrganizationVTX o where o.name like concat('%', :name, '%' ) and o.code like concat('%', :code, '%' ) ";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("name", name);
			q.setString("code", code);
			List<OrganizationVTX> ret = (List<OrganizationVTX>) q.list();
			log.debug("getAllOrg: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public List<OrganizationVTX> getAllOrgLevelRoot() throws DatabaseException {
		String qs = "from OrganizationVTX o where o.parent is null or o.parent = :param ";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("param", "-1");
			List<OrganizationVTX> ret = (List<OrganizationVTX>) q.list();
			log.debug("getAllOrg: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public void addUserToOrg(String userId, Long orgId) throws DatabaseException {
		log.debug("addUserToOrg({})");

		Session session = null;
		try {
//			OrganizationVTX organizationVTX = getOrganizationbyId(orgId);
//			List<String> orgIds = Arrays.asList(organizationVTX.getPath().split("/"));
			session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			/*for(String s : orgIds) {
				if(!s.equals("")) {

					UserOrganizationVTX userOrganizationVtx = new UserOrganizationVTX();
					userOrganizationVtx.setUserId(userId);
					userOrganizationVtx.setOrgId(Long.parseLong(s));

					session.save(userOrganizationVtx);
					session.flush();
					session.clear();
				}
			}*/
			UserOrganizationVTX userOrganizationVtx = new UserOrganizationVTX();
			userOrganizationVtx.setUserId(userId);
			userOrganizationVtx.setOrgId(orgId);

			session.save(userOrganizationVtx);
			session.flush();
			session.clear();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public List<User> findUsersbyOrg(String orgId) throws DatabaseException {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();

			Query q1 = session.createQuery("from OrganizationVTX o where o.path like '%/" + orgId + "/%' ");
			List<OrganizationVTX> orgs = q1.list();

			List<Long> orgSuit = new ArrayList<>();
			for (OrganizationVTX o : orgs) {
				orgSuit.add(o.getId());
			}

			Query q = session.createSQLQuery(
					"SELECT u.USR_ID as id, u.USR_NAME as name, u.USR_EMAIL as email " +
							"FROM OKM_USER u JOIN USER_ORG_VTX uo ON u.USR_ID = uo.USER_ID " +
							"where uo.ORG_ID IN :orgId"
			);
			q.setParameterList("orgId", orgSuit);
			List<User> ret = (List<User>) q.list();
			log.debug("getAllOrg: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public void removeUserOrg(Long orgId, String userId) throws DatabaseException {
		log.debug("removeUserOrg({})");

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			UserOrganizationVTX userOrganizationVTX = new UserOrganizationVTX();
			//userOrganizationVTX.setOrgId(orgId);
			userOrganizationVTX.setUserId(userId);

			session.beginTransaction();
			session.delete(userOrganizationVTX);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public void deleteOrg(Long orgId) throws DatabaseException {
		log.debug("removeUserOrg({})");
		String qs = "delete from ORGANIZATION_VTX where path like concat('%/', :orgId, '/%')";
		String qs1 = "delete from USER_ORG_VTX where ORG_ID IN :orgIds";
		String qs3 = "delete from ORG_DOC where org_id IN :orgIds";
		List<Long> orgIdsSuit = getAllLevelChild(orgId).stream().map(obj -> obj.getId()).collect(Collectors.toList());
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createSQLQuery(qs);
			q.setLong("orgId", orgId);
			session.beginTransaction();
			q.executeUpdate();
			session.getTransaction().commit();

			session.flush();
			session.clear();

			q = session.createSQLQuery(qs1);
			q.setParameterList("orgIds", orgIdsSuit);
			session.beginTransaction();
			q.executeUpdate();
			session.getTransaction().commit();

			session.flush();
			session.clear();

			q = session.createSQLQuery(qs3);
			q.setParameterList("orgIds", orgIdsSuit);
			session.beginTransaction();
			q.executeUpdate();
			session.getTransaction().commit();

		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public List<OrganizationVTX> getAllChild(Long parent) throws DatabaseException {
		String qs = "from OrganizationVTX o where o.parent = :parent ";
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setLong("parent", parent);
			List<OrganizationVTX> ret = (List<OrganizationVTX>) q.list();
			log.debug("getAllChild: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public List<OrganizationVTX> getAllLevelChild(Long parent) throws DatabaseException {
		String qs = "Select o.ID id, o.NAME name, o.CODE code, o.PATH path, o.ORG_PARENT parent" +
				" from ORGANIZATION_VTX o where o.path like concat('%/', :parent, '/%') ";

		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			SQLQuery q = session.createSQLQuery(qs);
			q.setLong("parent", parent);
			q.setResultTransformer(Transformers.aliasToBean(OrganizationVTX.class));
			q.addScalar("id", Hibernate.LONG);
			q.addScalar("name");
			q.addScalar("code");
			q.addScalar("parent", Hibernate.LONG);
			q.addScalar("path");
			List<OrganizationVTX> ret = (List<OrganizationVTX>) q.list();
			log.debug("getAllChild: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public String importUserToOrg(InputStream fileContent) {
		String NOT_EXIST = "Người dùng không tồn tại";
		String NOT_EXIST_ORG = "Đơn vị không tồn tại";
		String IN_ORG = "Người dùng đã được gán vào đơn vị khác";
		try {
			Workbook workbook = null;
			WorkbookSettings ws = new WorkbookSettings();
			ws.setEncoding("Cp1252");
			workbook = Workbook.getWorkbook(fileContent, ws);
			Sheet sheet = workbook.getSheet(0);
			String userNotExist = "";
			for (int i = 1; i < sheet.getRows(); ++i) {

				String userId = sheet.getCell(0, i).getContents();
				String orgCode = sheet.getCell(1, i).getContents();
				if(userId.equals("") || orgCode.equals("")) {
					userNotExist += "Dòng thứ " + String.valueOf(i+1) + "," + "thiếu dữ liệu,";
				}
				User user = AuthDAO.findUserByPk(userId);
				if (user == null) userNotExist += userId + "," + NOT_EXIST;
				else {

					OrganizationVTX organizationVTX = UserDAO.getInstance().getOrgByUserId(userId);
					if (organizationVTX == null) {
						OrganizationVTX orgByCode = getOrganizationbyCode(orgCode);
						if(orgByCode != null) {
							addUserToOrg(userId, orgByCode.getId());
						}
						else {
							userNotExist += orgCode + "," + NOT_EXIST_ORG;
						}
					}

					else {
						userNotExist += userId + "," + IN_ORG;
					}
				}
				userNotExist += ",";
			}
			return userNotExist;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void updateOrg(OrganizationVTXBean newOrg) throws DatabaseException {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			OrganizationVTX organizationVTX = new OrganizationVTX();
			organizationVTX.setId(newOrg.getId());
			organizationVTX.setCode(newOrg.getOrgCode());
			organizationVTX.setName(newOrg.getOrgName());
			organizationVTX.setParent(newOrg.getOrgParent());
			organizationVTX.setPath("");

			session.beginTransaction();
			session.update(organizationVTX);
			session.getTransaction().commit();

			if (newOrg.getOrgParent() == -1L) {
				organizationVTX.setPath('/' + String.valueOf(newOrg.getId()) + '/');
			} else {
				organizationVTX.setPath(getOrganizationbyId(newOrg.getOrgParent()).getPath() + newOrg.getId() + '/');
			}

			session.beginTransaction();
			session.save(organizationVTX);
			session.getTransaction().commit();
		} catch (HibernateException | DatabaseException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	public String importOrg(InputStream is) {
		String warn = "";
		try {

			Workbook workbook = null;
			WorkbookSettings ws = new WorkbookSettings();
			ws.setEncoding("Cp1252");
			workbook = Workbook.getWorkbook(is, ws);
			Sheet sheet = workbook.getSheet(0);

			for (int i = 1; i < sheet.getRows(); ++i) {
				String code = sheet.getCell(0, i).getContents(),
						name = sheet.getCell(1, i).getContents(),
						parent = sheet.getCell(2, i).getContents();

				OrganizationVTXBean newOrg = new OrganizationVTXBean();
				newOrg.setOrgCode(code);
				newOrg.setOrgName(name);

				if (parent != null && !parent.equals("")) {
					OrganizationVTX orgParent = getOrganizationbyCode(parent);
					if(orgParent == null) {
						warn += "Dòng thứ " + String.valueOf(i+1) + " " + parent + ",Mã đơn vị không tồn tại,";
						continue;
					}
					else {
						newOrg.setOrgParent(orgParent.getId());
					}
				} else {
					newOrg.setOrgParent(-1L);
				}

				createOrg(newOrg);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return warn;
	}
}
