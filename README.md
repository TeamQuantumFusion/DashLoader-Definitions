# DashLoader Github
Welcome to the codebase where DashLoader lives! Please report any issues you find with DashLoader here.
<center>
    <p align="center"  style="font-family: sans-serif, tahoma, arial, helvetica; font-size: 18px;">
        <!-- Features -->
        <img src="https://notalpha.dev/assets/dashloader/features-new.png" alt="Description" width="1000"/>
        <br/>
        This mod accelerates the Minecraft Asset Loading system by caching all of its content, This leads to a much faster
        game load.
        It does this by caching all of its content on first launch and on next launch loading back that exact cache.
        The cache loading is hyper fast and scalable which utilises your entire system.
        <br/><br/><br/>
        <strong>Important notes:</strong>
        <!-- Cache slowdown explanation -->
        <br/><br/> &bull; The first time your launch DashLoader it will be <strong>significantly slower</strong>.
        Because it needs to create a cache which contains all the assets minecraft normally loads.
        This will also happen every time you change a mod/resourcepack if that configuration does not have an existing
        cache.
        <!-- Mod compatibility -->
        <br/><br/> &bull; DashLoader has been known to be incompatible with a lot of mods.
        DashLoader 3.0 has massively improved compatibility by not forcing mod developers to add explicit support to make
        their assets cachable.
        This means that DashLoader will load assets normally for mod assets that cannot be cached.
        While this improves mod compatibility it hurts speed as the minecraft loading system is quite slow.
        <!-- Artists -->
        <br/><br/> &bull; If you use DashLoader for Developing mods or creating resource packs you can press
        <code>f3 + t</code> to recreate the cache to load your new assets in.
        If you want to just show off the speed of DashLoader you can press <code>shift + f3 + t</code>
        <br/><br/><br/>
        <!-- Community .-->
        <img src="https://notalpha.dev/assets/dashloader/community-new.png" alt="Community" width="1000"/>
        <br/>
        <img src="https://i.imgur.com/qbysL1T.png" alt="Discord" width="500"/>
        <!-- Sponsors -->
        <img src="https://notalpha.dev/assets/dashloader/sponsors-new.png" alt="Sponsors" width="1000"/>
        <br/>
        <a href="https://www.yourkit.com/java/profiler/">YourKit</a>
        Makes amazing profilers for both Java and .NET.
        We use their Java Profiler to understand where to optimize further and make DashLoader faster.
        <br/>
        <a href="https://www.jetbrains.com/">JetBrains</a>
        Creates excellent IDEs for all programmers and have provided us with access to their enterprise products for use to
        develop DashLoader and Hyphen.
        <!-- Donate -->
        <img src="https://notalpha.dev/assets/dashloader/donate-new.png" alt="Donate" width="1000"/>
        <br/>
        I have a <a href="https://ko-fi.com/notequalalpha">Ko-Fi page</a> if you would like to Support me. <br/>
        Please only support me if you like what I do, and you are not in a bad financial situation to do so.
    </p>
</center>
