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

	if [[ -d $LINE ]]; then
		>&2 echo "[DEBUG] $LINE is a directory"
		echo "<h3>"
		basename "$LINE" | perl -pe 's{_}{ }g' | perl -pe 's{^(.)}{\u$1};' | perl -pe 's{(\s)(.)}{$1\u$2};'
		echo "</h3>"
	elif [[ -f $LINE ]]; then
		>&2 echo "[DEBUG] $LINE is a file"
		echo "$LINE" | perl -pe 's{^(.*)$}{<img src="'$PREFIX'/$1" height=100>}g'
	else
		>&2 echo "[DEBUG] $LINE is neither a file nor dir"
	fi

done < "${1:-/dev/stdin}"

