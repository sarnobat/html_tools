##
#
# EXAMPLE
#
# 	Ubuntu:
#
#		zsh ~/computers.git/mac/bin/find_dirs_first.sh  ~/sarnobat.git/vhs/  						| tee /tmp/files.txt | bash ~/bin/file2img_recursive.sh | tee /tmp/files2.txt | sh ~/bin/file2img_html.sh 2>/dev/null
#		zsh ~/computers.git/mac/bin/find_dirs_first.sh  ~/sarnobat.git/www/channelz/images/hierarchical/ 2>/dev/null   	| tee /tmp/files.txt | bash ~/bin/file2img_recursive.sh | tee /tmp/files2.txt | sh ~/bin/file2img_html.sh 2>/dev/null  | tee ~/sarnobat.git/www/channelz/index.auto.html
# 	Mac:
#
# 		zsh /Volumes/git/computers.git/mac/bin/find_dirs_first.sh  ~/sarnobat.git/vhs/  | tee /tmp/files.txt | bash file2img_recursive.sh | tee /tmp/files2.txt | sh file2img_html.sh  2>/dev/null  | perl -pe 's{http://localhost:1156/webdav//}{}g' | tee /tmp/index.html
#

PREFIX=${1:-http://localhost:1156/webdav/}
PREVIOUS_LINE=""

while read LINE
do
	test -d "$LINE" && (
		>&2 echo "[debug] $LINE is a directory 2"
                echo "<h3>"
                basename "$LINE" | perl -pe 's{_}{ }g' | perl -pe 's{^(.)}{\u$1};' | perl -pe 's{(\s)(.)}{$1\u$2};'
                echo "</h3>"
	)
	test -f "$LINE" && (
		>&2 echo "[debug] $LINE is a file 2"
                echo "$LINE" | perl -pe 's{^(.*)$}{<img src="'$PREFIX'/$1" height=100>}g'
	)

done < "${1:-/dev/stdin}"

