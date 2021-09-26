$(function() {
  // toggle display function for class references
  let td = function() {
    let $a = $(this);
    let $ul = $a.parent().find('ul.class-references');
    $ul.slideToggle(Math.sqrt($ul.height()) * 25, function() {
      $a.children('i').toggleClass('hidden');
    });
  };
  $('ul.references > li > a.toggle-display').click(td);

  // find other class nodes for highlighting
  let h = function(span) {
    return $('span.class-node[data-name="' + $(span).data('name') + '"]');
  };
  $('span.class-node').mouseover(function() {
    h(this).addClass('highlight');
  }).mouseout(function() {
    h(this).removeClass('highlight');
  });

  // SVG: toggle display of node edges (for selected node)
  let tne = function() {
    let n = $(this).data('name');
    $(this).parents('svg').toggleClass('hover').find('path.' + n).toggleClass('show')
  };
  $('svg.dependency-graph a.node').mouseover(tne).mouseout(tne);

  // SVG: toggle display of single selected edge
  let tse = function() {
    $(this).prev().toggleClass('show').parents('svg').toggleClass('hover');
  };
  $('svg.dependency-graph path.tip').mouseover(tse).mouseout(tse).tooltipster({
    arrow: false,
    interactive: true,
    functionInit: function(instance, helper) {
      let max = 6;
      let ref = $(helper.origin).data('ref');
      let $li = $('#' + ref).children('ul').children();
      let $content = $('<ul></ul>');
      if ($li.length > max) $content.append($li.slice(0, max - 1).clone()).append($('<li><a class="more" href="#' + ref + '">' + ($li.length - max + 1) + ' more ...</a></li>'));
      else $content.append($li.clone());
      $content.find('a').data('tooltipster', instance);
      instance.content($content);
    }
  });
  $(document).on('click', '.tooltipster-content a.more', function() {
    let $a = $(this);
    let $t = $($a.attr('href'));
    if ($t.children('ul.class-references:hidden').length > 0) $t.children('a.toggle-display').each(td);
    $a.data('tooltipster').close();
  });
});
