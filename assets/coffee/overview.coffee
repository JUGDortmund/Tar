
refreshManualEntryForm = (data) ->
    console.log 'book it!'

# document ready
(($) ->
   $('[data-toggle="tooltip"]').tooltip()

   $('#employees').select2()
   $('#employees').off('select2-opening')
   $('.select2-search__field').focus();

   $('#employees').on 'select2:unselecting', (e) ->
      $(this).data 'state', 'unselected'
      return

   $('#employees').on 'select2:opening ', (e) ->
      if ($(this).data('state') == 'unselected')
         $(this).removeData 'state'
         e.preventDefault()
      return

   $('#clear').on 'click', (e) ->
      $('#employees').val(null).trigger('change')
      return

   $('.manual-entry').click ->
     $.ajax
       url: $(this).attr('href')
       dataType: "html"
       error: (jqXHR, textStatus, errorThrown) ->
         console.log(textStatus)
       success: (data) ->
         refreshManualEntryForm(data)
     return false


)(jQuery);