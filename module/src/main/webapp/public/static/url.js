const urlSearch = window.location.search
if (urlSearch && location.hash) {
  const href = window.location.href
  const hashIndex = href.indexOf(location.hash)
  const searchIndex = href.indexOf(location.search)
  if (hashIndex > searchIndex) {
    window.location.href = window.location.protocol + '//' + window.location.hostname + (window.location.port ? ':' + window.location.port : '') + location.pathname + location.hash + urlSearch
  }
}
