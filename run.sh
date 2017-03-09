echo '<link rel="stylesheet" type="text/css" href="http://netgear.rohidekar.com/portraits/style.css">' | tee out.html
cat rohidekar_family_tabs_fixed_cleansed.txt \
	| perl -pe 's{\t}{*}g'  \
	| perl -pe 's{(\*+)}{$1 }g' \
	| perl -pe 's{^}{*}g'  \
	| groovy list_to_html.groovy 2>/dev/null  \
	| tee -a out.html
cp out.html ~/sarnobat/www/portraits/tree.html