package com.testacl

import com.testacl.Report
import grails.plugin.springsecurity.acl.AclUtilService
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.PermissionFactory
import org.springframework.security.acls.model.Permission
import grails.gorm.transactions.Transactional
import org.springframework.security.core.parameters.P

class ReportService {

	PermissionFactory aclPermissionFactory
	AclUtilService aclUtilService
	def springSecurityService

	@PreAuthorize('hasPermission(#report, admin)')
	@Transactional
	void addPermission(@P("report") Report report, String username, int permission) {
		addPermission report, username, aclPermissionFactory.buildFromMask(permission)
	}

	@PreAuthorize('hasPermission(#report, admin)')
	@Transactional
	void addPermission(@P("report") Report report, String username, Permission permission) {
		aclUtilService.addPermission report, username, permission
	}

	@Transactional
	@PreAuthorize('hasRole("ROLE_USER")')
	Report create(String name, int number) {
		Report report = new Report(name, number)
		report.save()

		// Grant the current principal administrative permission
		addPermission report, springSecurityService.authentication.name, BasePermission.ADMINISTRATION

		report
	}

	@PreAuthorize('hasPermission(#id, "com.testacl.Report", read) or hasPermission(#id, "com.testacl.Report", admin)')
	Report get(@P("id") long id) {
		Report.get id
	}

	@PreAuthorize('hasRole("ROLE_USER")')
	@PostFilter('hasPermission(filterObject, read) or hasPermission(filterObject, admin)')
	List<Report> list(Map params) {
		Report.list params
	}

	int count() {
		Report.count()
	}

	@Transactional
	@PreAuthorize('hasPermission(#report, write) or hasPermission(#report, admin)')
	void update(@P("report") Report report, String name) {
		report.name = name
	}

	@Transactional
	@PreAuthorize('hasPermission(#report, delete) or hasPermission(#report, admin)')
	void delete(@P("report") Report report) {
		report.delete()

		// Delete the ACL information as well
		aclUtilService.deleteAcl report
	}
}
