package com.openkm.module;

import com.openkm.bean.OrganizationVTXBean;
import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Organization;
import com.openkm.dao.bean.OrganizationVTX;
import com.openkm.dao.bean.User;

import java.util.List;

public interface OrgVTXModule {

	public void createOrg(OrganizationVTXBean organizationVTXBean) throws DatabaseException;
	public List<OrganizationVTX> search(String name, String code) throws DatabaseException;
	public List<OrganizationVTX> getAllOrgLevelRoot() throws DatabaseException;
	public void addUserToOrg(String userId, Long orgId) throws DatabaseException;

	public List<User>  findUsersbyOrg(String orgId) throws DatabaseException;

    public void removeUserOrg(Long orgId, String userId) throws DatabaseException;

	public void deleteOrg(Long orgId) throws DatabaseException;
}
