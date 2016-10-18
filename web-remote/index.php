<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
	<title>SuperServer client</title>
	<script type="text/javascript" src="jquery-2.2.3.min.js"></script>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
  IP: <select id="ip" data-port="44556">
    <?php 
      for ($i=3; $i<=255; $i++){
          echo "<option>192.168.0.$i</option>";
      }
    ?>
  </select>
 
  <div class="borders">
    <button id="vol-up">VOLUME +</button>
    <button id="vol-down">VOLUME -</button>
    <br />
    <br />
    <div id="apps">
      <?php 
        for ($i=1; $i<=10; $i++){
            echo "<button class='appBtn' id='app-$i' data-appno='$i'>$i</button> ";
        }
      ?>
      <br/>
      <br/>
      <button id="desktop">Desktop (Win+D)</button>
      <button id="close">Close (Alt+F4)</button>
      <br/>
      <button id="enter">Enter</button>
      <button id="space">Space [_]</button>
      <button id="escape">Escape [Esc]</button>
      <br/>
      <br/>
      <p style="font-weight:bold">Control</p>
      <input type="text" id="command" size="1" maxlength="1" />
      <button id="control">Send</button>
      <br/>
      <br/>
      <p style="font-weight:bold">Teletype</p>
      <p style="font-size:small">Only a-zA-Z characters supported</p>
      <input type="text" id="text" size="10" />
      <button id="type">Send</button>
      <br/>
      <br/>
      <p style="font-weight:bold">Mouse</p>
      <input type="text" id="mm" size="10" />
      <button id="mouse-move">Move</button>
      <br/>
      <button id="click">Click</button>
      <button id="rclick">Right-Click</button>
      <br/>
    </div>
  </div>
  
 
  <script type="text/javascript">
    $(function(){
      
      var getHostAndPort = function(){ return ''+$('#ip').val()+':'+$('#ip').attr('data-port'); }
      var sendCommand = function(command){ $.ajax({url: "http://"+getHostAndPort()+"/"+command, dataType: "jsonp" }); }      
      
      $('#vol-up').click(function(){sendCommand("vol+");});
      $('#vol-down').click(function(){sendCommand("vol-");});
      $('#apps .appBtn').click(function(){sendCommand("app:"+$(this).attr('data-appno'));});
      $('#close').click(function(){sendCommand("close");});
      $('#escape').click(function(){sendCommand("esc");});
      $('#space').click(function(){sendCommand("space");});
      $('#enter').click(function(){sendCommand("enter");});
      $('#desktop').click(function(){sendCommand("desktop");});
      $('#mouse-move').click(function(){sendCommand("mm:"+$('#mm').val());});
      $('#click').click(function(){sendCommand("click");});
      $('#rclick').click(function(){sendCommand("rclick");});
      $('#control').click(function(){sendCommand("ctrl:"+$('#command').val());});
      $('#type').click(function(){sendCommand("type:"+$('#text').val());});

    });
  </script>
</body>
</html>
