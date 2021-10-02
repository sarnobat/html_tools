cd /home/sarnobat/github/cream && groovy httpcat_remove.groovy          --port=4476 --file=/home/sarnobat/sarnobat.git/src/javascript/cream/heap.txt 2>&1 ~/httpcat_remove.log

cd /home/sarnobat/github/cream && groovy httpgrep.groovy           	--port=4477 --file=/home/sarnobat/sarnobat.git/src/javascript/cream/heap.txt 2>&1 ~/httpgrep.log
cd ~/github/cream              && groovy httpcsvgraph_movenode.groovy                --port=4478 --file=/home/sarnobat/sarnobat.git/src/javascript/cream/heap.txt 2>&1 ~/httpcsvgraph_movenode.log

cat ~/sarnobat.git/src/javascript/d3_drag_drop_nodes/heap.txt | groovy ~/github/html_tools/csvHeap2listReversed.groovy | sh ~/bin/httpify.sh 2>/dev/null | sh ~/bin/htmlify.sh | tee /home/sarnobat/github/cream/out.html
