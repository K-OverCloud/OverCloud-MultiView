#!/bin/bash
#
# Copyright 2016 SmartX Collaboration (GIST NetCS). All rights reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#
# Name          : Install_Dependencies_vBox.sh
# Description   : Script for Getting VM's/Box
#
# Created by    : usman@smartx.kr
# Version       : 0.2
# Last Update   : October, 2018

#Source the Admin File
. /home/netcs/box-openstack-installation/queens/admin-openrc.sh

openstack server list --all-projects -c ID -c Name -c Status -c Host --long -f value  > SaaSVMs.list


