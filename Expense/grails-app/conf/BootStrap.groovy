class BootStrap {
     def authenticateService
     def init = { servletContext ->
     	createDefaultRoles()
     	createDefaultUsers()
        createRequestMap()
     }
     
     private def createDefaultRoles() {
	        if (!Authority.findByAuthority('ROLE_EMPLOYEE')) 
	            new Authority(authority:'ROLE_EMPLOYEE',description:"Employee").save()

	        if (!Authority.findByAuthority('ROLE_MANAGER'))
	            new Authority(authority:'ROLE_MANAGER',description:"Manager").save()

	        if (!Authority.findByAuthority('ROLE_ACCOUNTANT'))
	            new Authority(authority:'ROLE_ACCOUNTANT',description:"Accountant").save()
     }           

     private def createDefaultUsers() {
	        def employee = Person.findByUsername('employee')
	        if (!employee) {
	            employee = new Person(username:'employee',userRealName:'Employee',
	                passwd : authenticateService.passwordEncoder('welcome'),
                    email:'employee@lxisoft.com',enabled:true)
	            employee.save()
	        }

	        def manager = Person.findByUsername('manager')
	        if (manager == null) {
	            manager = new Person(username:'manager',userRealName:'Manager',
	                passwd : authenticateService.passwordEncoder('welcome'),
                    email:'manager@lxisoft.com',enabled:true)
	            manager.save()
	        }

	        def accountant  = Person.findByUsername('accountant')
	        if (accountant == null) {
	            accountant = new Person(username:'accountant',userRealName:'Accountant',
	                passwd : authenticateService.passwordEncoder('welcome'),
                    email:'accountant@lxisoft.com',enabled:true)
	            accountant.save()
	        }

	        Authority.findByAuthority('ROLE_EMPLOYEE').addToPeople(employee)
	        Authority.findByAuthority('ROLE_MANAGER').addToPeople(manager)
	        Authority.findByAuthority('ROLE_ACCOUNTANT').addToPeople(accountant)
    }
    
    private def createRequestMap() {
        new Requestmap(url: '/login/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
        new Requestmap(url: '/task/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/process/**', configAttribute: 'IS_AUTHENTICATED_FULLY').save()
        new Requestmap(url: '/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
    }
     
     def destroy = {
     }
}  
