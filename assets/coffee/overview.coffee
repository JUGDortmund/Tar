# document ready
(($) ->
   $('[data-toggle="tooltip"]').tooltip()

   $('.tag-filter').select2({
      tags: true,
      allowClear: true
   })
)(jQuery);