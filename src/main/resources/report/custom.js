$(function() {
  $('ul.references > li > a').click(function() {
    $(this).parent().find('ul.class-references').slideToggle();
  });

  let h = function(span, color, weight) {
    $('span.class-node[data-name="' + $(span).data('name') + '"]').css('color', color).css('font-weight', weight);
  };

  $('span.class-node').mouseover(function() {
    h(this, 'black', 'bold');
  }).mouseout(function() {
    h(this, 'dimgray', 'normal')
  })
});
