# Pagination component:

## Require (provide from higher level):
- Object as Page (reactive): page - include current page and max page

```ts
type Page = {
	current: number,
    max: number
}
```

- Function: gotoPage(page)
- Function: gotoPrePage()
- Function: gotoNxtPage()

## Example:
- Look at file: [NewsAndEvents.vue line 132](/src/pages/NewsAndEvents.vue)
