# Travis CI
[![Build Status](https://travis-ci.com/PeterYangIO/imhungry.svg?token=iWgvv2awSFFG9t2J6vtT&branch=master)](https://travis-ci.com/PeterYangIO/imhungry)

Contributors:   
Peter Yang (yangpete@usc.edu)   
Harrison Weinerman (hweinerm@usc.edu)  
Justin Lam (lam704@usc.edu)    
Jonathan Chai (chaijw@usc.edu)   
Samuel Rosas-Meza (rosasmez@usc.edu)  
       
Note: New repo used to remove API keys from public repo           

The travis config file is located at `.travis.yml` and is configured to run on any push to master.

| Property | Version |
| --- | --- |
| Ubuntu | Xenial |
| Java | 11 |
| MySQL | 5 |
| Tomcat | 7 |
* Note there are a few differences between the Travis CI versions and the development / production versions because of what Travis has installed

# Setup Instructions
## Backend
### Local Configuration
| Property | Version |
| --- | --- |
| Java | 11 |
| MySQL | 8 |
| Tomcat | 9 |

Using the above configurations, build the files within `src/main/java` and deploy on the `/` web directory.
This is necessary for the relative pathing used by the frontend to work properly.

Using `war:exploded` instead of `war` will allow for faster subsequent builds and deploys for development purposes.

## Frontend
The frontend is bundled with webpack. Bundles are not checked into version control.

### Installing Node Modules
1. Make sure to have [npm](https://www.npmjs.com/get-npm) Make sure to have at least version `6` of `npm`
2. `cd src/main/webapp`
3. `npm install`
4. `npm run build`

### Notes about npm
* Unlike Maven, IDEs are not typically configured to auto install npm packages so if there are any added npm packages, they need to be manually installed with `npm install`.
* The source files are contained in `src/main/webapp/src`.
  * The build files are contained in `src/main/webapp/static/js` which are referenced from the `.jsp` files.
