h2. Introduction

UJapid is a new encapsulation of japid as playframework-module, make japid templates can be stored in database and compiled at runtime (Of course for PROD mode of play).

About Japid Template Engine, you can get some useful information at ：
* https://github.com/branaway/Japid/blob/master/documentation/manual/Japid_Generic_Engine.textile 
* https://github.com/branaway/Japid/downloads

Yes, UJapid be equivalent to japidplay in Japid Template Engine. But UJapid suport for db store and dynamic compile. You should manage and publish your templates more easier.

h2. Installation

Download the zip package from Github repository, and decompression it into a directory. Of course you can move it to play/modules dirctory. 

h2. Configuration

There's very little different from Japid's configuration.

In application.conf
<pre>
	# Configuration of UJapid
	# =======================
	# Include UJapid module.
	module.ujapid=/path/to/ujapid

    # Template files store mode.:"file", "db", "net".
	ujapid.mode=file

	# Expire interval of template native cache. The unit is "ms". Default value is 2000. 
	ujapid.nativecache.source.expire=180000

    # Pattern of file filter. The template loader will load files by this pattern.  
	ujapid.filter=^japidviews/.*(\\.html|\\.json|\\.txt|\\.xml)$
</pre>

h2. Usage

Your controller must extends cn.uc.play.japid.mvc.UJapidController. Then you should using renderJapid() to render template.

h4. example:
Controller
<pre>
	public class MyController extends UJapidController {

    	public static void index() {
    		String name = "Robin Han";
    		renderJapid(name);
    	}
	}
</pre>

Template
<pre>
	`arg String name	
	&lt;html&gt;
	   &lt;head&gt;
	   	  &lt;title>sample&lt;/title&gt;
	   &lt;/head&gt;
	   &lt;body&gt;
	   	   My name is:${name}.
	   &lt;/body&gt;
	&lt;/html&gt;
</pre>

Output will like this:
<pre>
     My name is:Robin Han.
</pre>

More template grammar, please read Japid manual documents.