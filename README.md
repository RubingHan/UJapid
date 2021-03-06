1.Introduction

UJapid is a new encapsulation of japid as playframework-module, make japid templates can be stored in database and compiled at runtime (Of course for PROD mode of play).

About Japid Template Engine, you can get some useful information at ：
* https://github.com/branaway/Japid/blob/master/documentation/manual/Japid_Generic_Engine.textile 
* https://github.com/branaway/Japid/downloads

Yes, UJapid be equivalent to japidplay in Japid Template Engine. But UJapid suport for db store and dynamic compile. You should manage and publish your templates more easier.

2.Installation

Download the zip package from Github repository, and decompression it into a directory. Of course you can move it to play/modules dirctory. 

3.Configuration

There's very little different from Japid's configuration.

In application.conf
<pre>
# Configuration of UJapid
# =======================
# Include UJapid module.
module.ujapid=/path/to/ujapid

# Template files store mode.:"file", "db".
ujapid.mode=file

# Expire interval of template native cache. The unit is "ms". Default value is 180000. Used for all mode.
ujapid.navitecache.expire=2000

# Expire interval of template remote cache (memcached). The unit is "ms". Default value is 3600000. Used for db mode.
ujapid.remotecache.expire=10000

# Pattern of file filter. The template loader will load files by this pattern.  
ujapid.filter=^japidviews/.*(\\.html|\\.json|\\.txt|\\.xml)$

# For db mode.
memcached=enabled
memcached.host=127.0.0.1:11211
db=mysql://username:password@host/database
</pre>

4.DB Table and Memcache

If you want to use db mode, create TEMPLATE table in DB with sql script below. 
<pre>
CREATE TABLE `TEMPLATE` (
  `id` varchar(100) NOT NULL DEFAULT '',
  `source` longtext NOT NULL,
  `last_modify` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `type` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `typeIdx` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
</pre>


5.Template data procedure in db mode.

 1) On application staring, UJapid load all templates from db, set theirs last modified time into memcached, and compiled them;

 2) On a request procedure, UJapid get template from native cache first;

 3) If the themplate in native cache has been expired, check the last modified time in memcached. If it has been expired, reload the template from db;
 
 4) You can modify and publish the templates in your managment console system. You must update last modified time in db and memcached.


6.Coding

Your controller must extends cn.uc.play.japid.mvc.UJapidController. Then you should using renderJapid() to render template.

example:

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
`args String name	
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