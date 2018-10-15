var MongoClient = require('mongodb').MongoClient;
var Db = require('mongodb').Db;
var Connection = require('mongodb').Connection;
var Server = require('mongodb').Server;
var BSON = require('mongodb').BSON;
var ObjectID = require('mongodb').ObjectID;
var dateFormat  = require('dateformat');
var mongourl = "mongodb://103.22.221.56:27017/overclouddb";

ResourceProvider = function() {};
//UserProvider = function() {};

//Get MultiView Users
ResourceProvider.prototype.getUsers = function(callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
        var collection = db.collection("configuration-multiview-users");
        collection.find().toArray(function(err, users){
            callback(null,users);
		});
    });
};

//Get Controllers List From MongoDB New
ResourceProvider.prototype.getControllerList = function(callback) 
{
	MongoClient.connect(mongourl, function(err, db)
    {
        var collection = db.collection("playground-controllers-list");
        collection.find({},{controllerIP: true, controllerName: true, controllerStatus: true, controllerSoftware: true, _id: false}).sort({controllerName: -1}).toArray(function(err, controllers){
	     	callback(null, controllers);
		    db.close();
		});
	 });
};

//Get pBoxes List From MongoDB
ResourceProvider.prototype.getpBoxList = function(callback) 
{
    MongoClient.connect(mongourl, function(err, db)
    {
        console.log('Physical Boxes List: ');
		// Locate all the entries using find
        var collection = db.collection("configuration-pbox-list");
        //collection.find({type: 'B**'},{box: true, host: true, management_ip: true, management_ip_status: true, data_ip: true, data_ip_status: true, control_ip: true, control_ip_status: true, _id: false}).sort({host: -1}).toArray(function(err, boxes){
		//collection.find({$or:[{type: 'B**'},{type: 'C**'}]},{box: true, boxID: true, management_ip: true, management_ip_status: true, data_ip: true, data_ip_status: true, control_ip: true, control_ip_status: true, _id: false}).sort({host: -1}).toArray(function(err, boxes){
		collection.find( {}, {box: true, boxID: true, management_ip: true, management_ip_status: true, data_ip: true, data_ip_status: true, control_ip: true, control_ip_status: true, _id: false}).sort({host: -1}).toArray(function(err, boxes){
			//	db.close();
			callback(null,boxes);
	});
	//console.log (db.boxes);
    });
};

//Get vSwitches List From MongoDB
ResourceProvider.prototype.getvSwitchList = function(callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
        console.log('OVS bridges List: ');
        var collection = db.collection("configuration-vswitch-list");
        collection.find({},{type: true, bridge: true, topologyorder: true, _id: false}).sort({topologyorder: 1}).toArray(function(err, switchList){
			//db.close();
			callback(null,switchList);
        });
    });
};

//Get OpenStack Instances List From MongoDB
ResourceProvider.prototype.getvBoxList = function(callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
        console.log('OpenStack instances List: ');
        var collection = db.collection("configuration-vbox-list");
        collection.find({},{box: true, name: true, osusername: true, ostenantname: true, vlanid: true, state: true, _id: false}).sort({box: -1}).toArray(function(err, vBoxList){
                //db.close();
                callback(null,vBoxList);
        });
    });
};

//Get Services List From MongoDB
ResourceProvider.prototype.getServicesList = function(callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
        console.log('Services List: ');
        var collection = db.collection("configuration-service-list");
        collection.find({type: 'B**'},{box: true, name: true, osusername: true, ostenantname: true, vlanid: true, state: true, _id: false}).sort({box: -1}).toArray(function(err, vmList){
                //db.close();
                callback(null,vmList);
        });
    });
};

//Get pPath Status From MongoDB
/*ResourceProvider.prototype.getpPathStatus = function(callback)
{
    MongoClient.connect('mongodb://210.125.84.69:27017/smartxdb', function(err, db)
    {
        console.log('Physical Path Status: ');
        var collection = db.collection("configuration-pbox-path-status");
        collection.find({},{box: true, m_ip: true, d_ip: true, c_ip: true, _id: false}).sort({box: -1}).toArray(function(err, pPathStatus){
                //db.close();
                callback(null,pPathStatus);
        });
    });
};*/

//Get OVS Bridge Status From MongoDB
ResourceProvider.prototype.getovsBridgeStatus = function(callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
        console.log('OVS Bridge Status: ');
        var collection = db.collection("configuration-vswitch-status");
        collection.find({},{boxID: true, bridge: true, status: true, _id: false}).sort({boxID: -1}).toArray(function(err, ovsBridgeStatus){
                //db.close();
                callback(null,ovsBridgeStatus);
        });
    });
};

//Get Operator Controller Flow Rules
ResourceProvider.prototype.getOpsSDNConfigList = function(boxID, callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
	console.log('Flow Rules List: '+boxID);
	var currentTime = new Date();
	console.log('System Time: '+currentTime);
	dateFormat(currentTime.setMinutes(currentTime.getMinutes() - 59));
	console.log('Updated Time: '+currentTime);
       	var collection = db.collection("flow-configuration-sdn-controller-rt");
        collection.find({controllerIP: '103.22.221.152', boxID: boxID},{_id: false}).sort({name: -1}).toArray(function(err, rulesList){
                //db.close();
                callback(null,rulesList);
        });
    });
};

//Get Operator Controller Flow Statistics
ResourceProvider.prototype.getOpsSDNStatList = function(boxID, callback)
{
    MongoClient.connect(mongourl, function(err, db)
    {
        console.log('Flow Statistics List: ');
        var currentTime = new Date();
        console.log('System Time: '+currentTime);
        dateFormat(currentTime.setMinutes(currentTime.getMinutes() - 5));
        console.log('Updated Time: '+currentTime);
        var collection = db.collection("flow-stats-sdn-controller-rt");
        collection.find({controllerIP: '103.22.221.152', boxID: boxID},{_id: false}).sort({name: -1}).toArray(function(err, statList){
            //db.close();
            callback(null,statList);
        });
    });
};

ResourceProvider.prototype.getDataMultiSliceVisibility = function(userID, callback)
{
    MongoClient.connect(mongourl, function(err, client){
		//const db = client.db('overclouddb');

		var colUnderlay_main = client.collection('underlay_main');
		var playground_sites = client.collection('playground_sites');
		var colpBox = client.collection('pbox-list');
		var flowvisor_slice = client.collection('flowvisor_slice');
		var colVMInstance = client.collection('vm-instance-list');
		
		var data = [];
		var main = 0;
		
		colUnderlay_main.find({}).toArray(function(err, rUnderlay_main){
			//colunder_int.find({}).toArray(function(err, rUnder_int){
				//colunder_ren.find({}).toArray(function(err, rUnder_ren){
					playground_sites.find({}).toArray(function(err, rplayground_sites){
						colpBox.find({}).toArray(function(err, rpBox){
							flowvisor_slice.find({}).toArray(function(err, rVLANs){
								colVMInstance.find({}).toArray(function(err, rVM){
									//colIoTHostList.find({}).toArray(function(err, rIoT){
										//KREONET Main
										for (var i = 0 ; i < rUnderlay_main.length; i++){
											rUnderlay_main[i].drilldown = [];
											rUnderlay_main[i].resource = 4;
											rUnderlay_main[i].label = rUnderlay_main[i].name;
											rUnderlay_main[i].info = rUnderlay_main[i].name;
											rUnderlay_main[i].color = 'white';
											rUnderlay_main[i].textBoder= 'LightGrey';
											
											//Playground sites
											for (var l = 0 ; l < rplayground_sites.length; l++){
												if (rplayground_sites[l].mainID == rUnderlay_main[i].mainID)
												{
													rplayground_sites[l].drilldown = [];
													rplayground_sites[l].resource = 6;
													rplayground_sites[l].label =   rplayground_sites[l].name;
													rplayground_sites[l].info = "Site Info \n Name: " + rplayground_sites[l].name;
													rplayground_sites[l].color = 'white';
													rplayground_sites[l].colorBoder =  	'#9fdf9f';
													
													rUnderlay_main[i].drilldown.push(rplayground_sites[l]);
													
													//Physical Boxes
													for (var m = 0 ; m < rpBox.length; m++){
														if (rpBox[m].site == rplayground_sites[l].siteID)
														{
															rpBox[m].drilldown = [];
															rpBox[m].resource = 1;
															rpBox[m].label = ''+ rpBox[m].box;
															rpBox[m].info = "Box Info \n Box Name: "+rpBox[m].boxID+ "\n"+" Site: " + rpBox[m].site;
															if (rpBox[m].data_ip_status == "GREEN"){
																rpBox[m].color = 'white';
																//console.log(rpBox[m].boxName+ " "+rpBox[m].data_ip_status);
															}
															else{
																rpBox[m].color = '#ffffb3';//rpBox[m].data_ip_status; light yellow
															}
															rpBox[m].colorBoder = 	'#53c653' //MediumSeaGreen
															
															rplayground_sites[l].drilldown.push(rpBox[m]);
															
															//Tenant VLAN IDs
															for (var n = 0 ; n < rVLANs.length; n++){
																if (rVLANs[n].boxID == rpBox[m].boxID)
																{
																	rVLANs[n].drilldown = [];
																	rVLANs[n].resource = 7;
																	rVLANs[n].label = ''+rVLANs[n].VLANID;
																	rVLANs[n].info= "VLAN: " +rVLANs[n].VLANID+ "\n" + " Box: " + rVLANs[n].boxID;
																	rVLANs[n].color = 'white';
																	rVLANs[n].colorBoder = '#ffffb3'; //Navajo white
																	rpBox[m].drilldown.push(rVLANs[n]);
																	
																	//OpenStack VMs
																	for (var o = 0 ; o < rVM.length; o++){
																		console.log(rVM[o].boxName+ " "+rVM[o].state);
																		if (rVM[o].box == rVLANs[n].boxID)
																		{
																			rVM[o].drilldown = [];
																			rVM[o].resource = 3;
																			rVM[o].label = ''+rVM[o].name;
																			rVM[o].info = "VM info \n Name: " +rVM[i].name + " - Box: " + rVM[i].boxID;
																			rVM[o].colorBoder = '#ffff1a'; //Gold
																			
																			if (rVM[o].state == "Running"){
																				rVM[o].color = 'white';
																			//	console.log(rVM[o].box+ " "+rVM[o].state);
																			}
																			else{
																				rVM[o].color = "#ffcce0"; //light red
																			}
																			rVLANs[n].drilldown.push(rVM[o]);
																			
																			var sFlows = {"resource": "11", "label": "SF", "info": "Click to get details about sampled flows", "color": "white", "colorBoder": "#80b3ff"}; //Blue
																			rVM[o].drilldown.push(sFlows);
																			
																			sFlows.drilldown = [];
																			//var tPackets = {"resource": "12", "label": "TP", "info": "Click to get details about packets", "color": "white", "colorBoder": "#0040ff"}; //Blue
																			//sFlows.drilldown.push(tPackets);
																		}
																	}
																	
																}
															}
														}
													}
												}
											}
											data = rUnderlay_main;
											//console.log(data);
										}
										callback(null, data);
									});
								});
							});
						});
					});
				//});
			//});
		//});
    });
};

exports.ResourceProvider = ResourceProvider;
//exports.UserProvider = UserProvider;
