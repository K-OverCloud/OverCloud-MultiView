#Update This Script Before Installing MultiView Software

use smartxdb
show collections

db.configuration-pbox-list.ensureIndex({box:1, boxID: 1},{unique:true})
db.configuration-pbox-list.insert( { box: "SaaS-Box1", boxID: "OverCloudBox1", management_ip: "", management_ip_status: "up", data_ip: "10.10.20.51", data_ip_status: "up", control_ip: "192.168.88.200", control_ip_status: "up", ovs_vm1: "", ovs_vm2: "", active_ovs_vm: "" } )
db.configuration-pbox-list.insert( { box: "SaaS-Box2", boxID: "OverCloudBox2", management_ip: "", management_ip_status: "up", data_ip: "10.10.20.52", data_ip_status: "up", control_ip: "192.168.88.201", control_ip_status: "up", ovs_vm1: "", ovs_vm2: "", active_ovs_vm: "" } )
db.configuration-pbox-list.insert( { box: "SaaS-Box3", boxID: "OverCloudBox3", management_ip: "", management_ip_status: "up", data_ip: "10.10.20.53", data_ip_status: "up", control_ip: "192.168.88.202", control_ip_status: "", ovs_vm1: "", ovs_vm2: "", active_ovs_vm: "" } )
db.configuration-pbox-list.insert( { box: "SaaS-Box4", boxID: "OverCloudBox4", management_ip: "", management_ip_status: "up", data_ip: "10.10.20.54", data_ip_status: "up", control_ip: "210.125.84.203", control_ip_status: "up", ovs_vm1: "", ovs_vm2: "", active_ovs_vm: "" } )

#Insert OVS Bridges Topology Data into Collection
db.configuration-vswitch-list.ensureIndex({type:1, bridge: 1},{unique:true})
db.configuration-vswitch-list.insert( { type: "OverCloud", bridge: "br-tun", topologyorder: "1" } )
db.configuration-vswitch-list.insert( { type: "OverCloud", bridge: "br-ex", topologyorder: "1" } )
db.configuration-vswitch-list.insert( { type: "OverCloud", bridge: "br-int", topologyorder: "2" } )

#Insert OVS Bridges Status Data into Collection <Insert For all boxes>
db.configuration-vswitch-status.ensureIndex({bridge:1, box: 1},{unique:true})
db.configuration-vswitch-status.insert( { bridge: "br-tun", box: "SaaS-Box1", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-ex", box: "SaaS-Box1", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-int", box: "SaaS-Box1", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-tun", box: "SaaS-Box2", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-ex", box: "SaaS-Box2", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-int", box: "SaaS-Box2", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-tun", box: "SaaS-Box3", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-ex", box: "SaaS-Box3", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-int", box: "SaaS-Box3", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-tun", box: "SaaS-Box4", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-ex", box: "SaaS-Box4", status: "RED" } )
db.configuration-vswitch-status.insert( { bridge: "br-int", box: "SaaS-Box4", status: "RED" } )