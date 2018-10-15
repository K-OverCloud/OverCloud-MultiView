COLLECTOR_IP=210.125.84.69
COLLECTOR_PORT=6343
AGENT_IP=br-ex
HEADER_BYTES=128
SAMPLING_N=64
POLLING_SECS=10

for BRIDGE in br-int ; do
sudo ovs-vsctl -- --id=@sflow create sflow agent=${AGENT_IP} target=\"${COLLECTOR_IP}:${COLLECTOR_PORT}\" header=${HEADER_BYTES} sampling=${SAMPLING_N} polling=${POLLING_SECS} -- set bridge $BRIDGE sflow=@sflow
done;
