function CreateTable(prams) {
  var data = prams,
    way = {
    createRow: function (value, type) {
      var domClass = !!type ? ' ct_table_head' : '',
          domStyle = !!type ? ' font-weight: bold;' : '';
      return '<div class="ct_table_row '+domClass+'" style="display:table-row;'+domStyle+'">'+value+'</div>';
    },
    createCell: function (value, style) {
      var domStyle = style || '';
      return '<div class="ct_table_cell" style="display:table-cell;padding:10px;border-left:solid 1px #e9eaec;border-bottom:solid 1px #e9eaec;"><div style="'+domStyle+'">'+value+'</div></div>';
    },
    createHead: function () {
      var html = '';
      for(var i = 0, l = data.head.length; i < l; i++) {
        html += way.createCell(data.head[i].value, !!data.head[i].width ? 'width:' + data.head[i].width : 'width:100px; white-space: nowrap;');
      }

      return way.createRow(html, 'head');
    },
    createBody: function () {
      var html = '';
      for(var i = 0, l = data.body.length; i < l; i++) {
        var row = '';
        for (var ic = 0, lc = data.head.length; ic < lc; ic++) {
          row += way.createCell(data.body[i][ic]);
        }
        html += way.createRow(row);
      }

      return html;
    }
  };

  var content = document.getElementById(data.dom);
  content.innerHTML = '<div class="ct_table" style="display:table;width:100%;border-top:solid 2px #e9eaec;border-right:solid 1px #e9eaec;color:#495060;font-size:12px;line-height: 20px;">'+ way.createHead() + way.createBody() +'</div>';
}