$(function() {
  $('ul.references > li > a.toggle-display').click(function() {
    let $a = $(this);
    let $ul = $a.parent().find('ul.class-references');
    $ul.slideToggle(Math.sqrt($ul.height()) * 25, function() {
      $a.children('i').toggleClass('hidden');
    });
  });

  let h = function(span) {
    return $('span.class-node[data-name="' + $(span).data('name') + '"]');
  };

  $('span.class-node').mouseover(function() {
    h(this).addClass('highlight');
  }).mouseout(function() {
    h(this).removeClass('highlight');
  });

  let et = function() {
    let n = $(this).data('name');
    $(this).parents('svg').toggleClass('hover').find('path.' + n).toggleClass('show')
  };
  $('svg.dependency-graph a.node').mouseover(et).mouseout(et);
});
