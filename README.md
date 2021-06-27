# Users Management FullSack Web Application

Fullstack Web App for handling users, with Spring and Angular : *JSON Web Token (JWT) with Spring Security And Angular*

## User Domain

We create the User model for our application. We create another User model that will be used for Spring Security. This one is the **UserPrincipal** and implements the **UserDetails**.    


We also use JPA Repositories with Spring Data JPA for querying operations foward the Database.

## Security 

- We put all the util components and Constants that will be used to config the **WebSecurityConfiguration** by using **JWT for Athentication**. 
- There are the filters (authentication and authorization)
- The JWT token provider

## Exception Handling

Here we custom the Exception and handle them in a sexier way and efficiently.
