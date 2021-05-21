$(function() {
  $('ul.references > li > a').click(function() {
    $(this).parent().find('ul.class-references').slideToggle();
  });

  let h = function(span) {
    return $('span.class-node[data-name="' + $(span).data('name') + '"]');
  };

  $('span.class-node').mouseover(function() {
    h(this).addClass('highlight');
  }).mouseout(function() {
    h(this).removeClass('highlight');
  });
});
