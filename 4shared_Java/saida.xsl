<?xml version="1.0" encoding="iso-8859-1"?><!DOCTYPE xsl:stylesheet  [	<!ENTITY nbsp   " ">	<!ENTITY copy   "©">	<!ENTITY reg	"®">	<!ENTITY trade  "™">	<!ENTITY mdash  "—">	<!ENTITY ldquo  "“">	<!ENTITY rdquo  "”"> 	<!ENTITY pound  "£">	<!ENTITY yen	"¥">	<!ENTITY euro   "€">]>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="iso-8859-1"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
				<meta name="description" content="" />
				<meta name="keywords" content="" />
				<meta name="author" content="" />
				<meta name="language" content="pt-br" />
				<title></title>
				<link rel="stylesheet" href="estilos/layout.css" />
				<link rel="stylesheet" href="estilos/content.css" />
			</head>
			<body>
				<div id="base">
					<!-- CONTENT *********************************************** -->
					<div id="content">	<!--/side-bar -->
						<div id="content-site">	<!-- CONTENT SITE *********************************************** -->
							
							<xsl:for-each select="root/img">
								<img src="<xsl:value-of select="tag6" />" />
							</xsl:for-each><!-- /CONTENT SITE *********************************************** -->
						</div><!-- /content-site -->
					</div><!-- /content --><!-- /CONTENT *********************************************** -->
					
					<!-- FOOTER *********************************************** -->
					<div id="footer">
						<img src="images/rod_baselat.gif" class="base" />
						<div class="menu">
							<span>
								<a href="#">Galeria de Fotos</a>
								|
								<a href="#">Livro de Visitas</a>
								|
								<a href="#">Notícias</a>
								|
								<a href="#">A Cidade</a>
								|
								<a href="#">Contato</a>
							</span>
						</div>
						
					</div><!-- footer --><!-- /FOOTER *********************************************** -->
				</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>