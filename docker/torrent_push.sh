#!/bin/sh

# telegram configuration
SERVER="9091 --auth admin:1111"

URL='http://localhost:7021/torrent/complate?'

sleep 1s

# torrent remove
TORRENTLIST=`transmission-remote $SERVER --list | sed -e '1d;$d;s/^ *//' | cut --only-delimited --delimiter=" " --fields=1`
for TORRENTID in $TORRENTLIST
do
    DL_COMPLETED=`transmission-remote $SERVER --torrent $TORRENTID --info | grep "Percent Done: 100%"`
    STATE_STOPPED=`transmission-remote $SERVER --torrent $TORRENTID --info | grep "State: Seeding\|Stopped\|Finished\|Idle"`
    if [ "$DL_COMPLETED" ] && [ "$STATE_STOPPED" ]; then
        transmission-remote $SERVER --torrent $TORRENTID --remove
    fi
done

# telegram notify go
res=$(/usr/bin/curl --data-urlencode "msg=$TR_TORRENT_NAME" "$URL")
