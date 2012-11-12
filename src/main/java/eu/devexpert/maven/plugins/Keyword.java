package eu.devexpert.maven.plugins;

import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSExpression;
import com.phloc.css.decl.CSSSelector;
import com.phloc.css.decl.CSSSelectorSimpleMember;

public enum Keyword {
	TEXT_ALIGN_RIGHT("text-align-right", "text-align", "right"),
	TEXT_ALIGN_LEFT("text-align-left", "text-align", "left"),
	FULL_HEIGHT("height-full", "height", "100%"),
	FULL_WIDTH("width-full", "width", "100%"),
	ALIGIN_RIGTH("align-right", "float", "right"),
	ALIGIN_LEFT("align-left", "float", "left");

	private String	cssName;
	private String	declaration;
	private String	term;

	Keyword(String cssName, String declaration, String term) {
		this.cssName = cssName;
		this.declaration = declaration;
		this.term = term;
	}

	public CSSDeclaration getDeclaration() {
		CSSExpression cex = new CSSExpression();
		cex.addTermSimple(term);
		return new CSSDeclaration(declaration, cex, false);
	}

	public CSSSelector getSelector() {
		CSSSelector selector = new CSSSelector();
		selector.addMember(new CSSSelectorSimpleMember("." + cssName));
		return selector;
	}

	public String getName() {
		return cssName;
	}
}
