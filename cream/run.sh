cd /home/sarnobat/github/cream && groovy httpcat_remove.groovy          --port=4476 --file=/home/sarnobat/sarnobat.git/src/javascript/cream/heap.txt 2>&1 ~/httpcat_remove.log

cd /home/sarnobat/github/cream && groovy httpgrep.groovy           	--port=4477 --file=/home/sarnobat/sarnobat.git/src/javascript/cream/heap.txt 2>&1 ~/httpgrep.log

#
cat ~/sarnobat.git/src/javascript/d3_drag_drop_nodes/heap.txt | groovy ~/github/html_tools/csvHeap2listReversed.groovy | sh ~/bin/httpify.sh | xargs -n 1 -d'\n' sh ~/bin/htmlify.sh | tee ~/trash/hoist.html
