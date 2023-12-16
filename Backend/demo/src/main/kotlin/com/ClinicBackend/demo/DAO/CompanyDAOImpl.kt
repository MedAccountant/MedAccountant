package com.ClinicBackend.demo.DAO

import com.ClinicBackend.demo.Entities.Company
import com.ClinicBackend.demo.Entities.Department
import com.ClinicBackend.demo.Entities.Supplier
import com.ClinicBackend.demo.Entities.User
import com.ClinicBackend.demo.Repos.CompanyRepos
import com.ClinicBackend.demo.Repos.DepartmentRepos
import com.ClinicBackend.demo.Repos.SupplierRepos
import com.ClinicBackend.demo.Repos.UserRepos
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component("CompanyDAOImpl")
class CompanyDAOImpl:CompanyDAO {
    @Autowired
    lateinit private var companyRepos: CompanyRepos

    @Autowired
    lateinit private var departmentRepos: DepartmentRepos

    @Autowired
    lateinit private var userRepos:UserRepos

    @Autowired
    lateinit private var supplierRepos: SupplierRepos

    //company management
    override fun getCompanies()=companyRepos.findAll()
    override fun getCompanyByName(companyName: String)=companyRepos.findByCompanyName(companyName)
    override fun createCompany(company: Company)=companyRepos.save(company).companyName
    override fun deleteCompany(companyName: String){ companyRepos.delete(companyRepos.findByCompanyName(companyName)!!) }
    override fun editCompany(oldCompanyName: String, newCompany: Company): String? {
        val oldCompany=companyRepos.findByCompanyName(oldCompanyName)!!
        oldCompany.editCompany(newCompany)
        companyRepos.save(oldCompany)
        return newCompany.companyName
    }

    //departments management
    override fun getDepartmentsFromCompany(companyName: String)=companyRepos.findByCompanyName(companyName)!!.departments//departmentRepos.findByCompany(companyRepos.findByCompanyName(companyName)!!)
    override fun getDepartmentByNameAndCompany(departmentName: String, companyName: String)
    = departmentRepos.findByDepartmentNameAndCompany(departmentName,companyRepos.findByCompanyName(companyName)!!)
    override fun addDepartment(department: Department)=departmentRepos.save(department).departmentName
    override fun deleteDepartmentFromCompany(departmentName: String, companyName: String) {
        val company=companyRepos.findByCompanyName(companyName)!!
        val department=departmentRepos.findDepartmentWithUsersByDepartmentName(departmentName)
        department?.users?.forEach { it.removeDepartment(department) }
        userRepos.saveAll(department!!.users)
        departmentRepos.deleteByDepartmentNameAndCompany(departmentName,company)
    }
    override fun editDepartment(oldDepartmentName: String, newDepartment: Department,companyName: String): String? {
        val oldDepartment=departmentRepos.findByDepartmentNameAndCompany(oldDepartmentName,companyRepos.findByCompanyName(companyName)!!)!!
        oldDepartment.editDepartment(newDepartment)
        departmentRepos.save(oldDepartment)
        return newDepartment.departmentName
    }

    //user management
    override fun getAllUsersFromCompany(companyName: String)=
        userRepos.findByDepartmentsIn(companyRepos.findByCompanyName(companyName)!!.departments)
    override fun addUser(newUser: User)=userRepos.save(newUser).login
    override fun deleteUser(login:String) {
        val userToDelete=userRepos.findByLogin(login)!!
    // userToDelete.departments.forEach { it.deleteUser(userToDelete) }
        userRepos.delete(userToDelete)
    }
    override fun editUser(oldUserLogin: String, newUser: User,companyName: String): String? {
        val oldUser=userRepos.findByLogin(oldUserLogin)
        println("oldUserLogin: $oldUserLogin, oldUserFind: $oldUser")
        println("====================================")
        println("newUser: $newUser")
        oldUser?.editUser(newUser)
        userRepos.save(oldUser!!)
        return newUser.login
    }

    //suppler management
    override fun getAllSuppliersFromCompany(companyName: String)=
        supplierRepos.findByDepartmentsIn(companyRepos.findByCompanyName(companyName)!!.departments)
    override fun addSupplier(newSupplier: Supplier)=supplierRepos.save(newSupplier).email
    override fun deleteSupplier(email:String) {
        val supplierToDelete=supplierRepos.findByEmail(email)!!
        // supplierToDelete.departments.forEach { it.deleteSupplier(supplierToDelete) }
        supplierRepos.delete(supplierToDelete)
    }
    override fun editSupplier(oldSupplierEmail: String, newSupplier: Supplier,companyName: String): String? {
        val oldSupplier=supplierRepos.findByEmail(oldSupplierEmail)
        println("oldSupplierLogin: $oldSupplierEmail, oldSupplierFind: $oldSupplier")
        println("====================================")
        println("newSupplier: $newSupplier")
        oldSupplier?.editSupplier(newSupplier)
        supplierRepos.save(oldSupplier!!)
        return newSupplier.email
    }
}