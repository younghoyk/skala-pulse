import { defineStore } from 'pinia'
import { reactive } from 'vue'
import { mockPendingSignups, mockMembers } from '@/data/mockAdminMembers'

export const useMembersStore = defineStore('members', () => {
  const pending = reactive([...mockPendingSignups])
  const members = reactive([...mockMembers])

  function requestSignup({ name, email, requestedRole }) {
    pending.push({ id: `p-${Date.now()}`, name, email, requestedRole })
  }

  function approve(signup) {
    const idx = pending.findIndex((p) => p.id === signup.id)
    if (idx === -1) return
    pending.splice(idx, 1)
    members.unshift({ id: signup.id, name: signup.name, email: signup.email, role: signup.requestedRole })
  }

  function reject(id) {
    const idx = pending.findIndex((p) => p.id === id)
    if (idx !== -1) pending.splice(idx, 1)
  }

  function deleteMember(member) {
    const idx = members.findIndex((m) => m.id === member.id)
    if (idx !== -1) members.splice(idx, 1)
  }

  return { pending, members, requestSignup, approve, reject, deleteMember }
})
