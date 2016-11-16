cat rohidekar_family_tabs_fixed_cleansed.txt \
	| perl -pe 's{\t}{*}g'  \
	| perl -pe 's{(\*+)}{$1 }g' \
	| perl -pe 's{^}{*}g'  \
	| groovy list_to_html.groovy 2>/dev/null  \
	| pbcopy