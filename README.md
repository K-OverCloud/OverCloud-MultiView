# SmartX MultiView Visibility Framework
## Summary ##
### Overview ###
SmartX MultiView Visibility Framework is under development at NetCS Lab (Networked Computing Systems Laboratory) to provide Multi-level Visibility Support for OpenStack Cloud and OpenvSwitch OpenFlow-SDN Infrastructures.

### Release Version ###
This is the first version of the tools and it still being developed/verified.

### Current Support and Caveats ###
* Ubuntu Operating System 14.04.02 (Trusty Tahr)
* OpenStack Juno Stable Release
* OpenStack VLAN-based Tenant Network
* OpenvSwitch OpenFlow-SDN VXLAN Overlay Network and ODL

### Installation ###
Before Installation modify the follwing configuration files:
* MultiView_Configurations/Custom_Collectors.properties
* MultiView_Scripts/MultiView_Configuration_Database.js

After Modification of above Files Execute:
* ./Install.sh
