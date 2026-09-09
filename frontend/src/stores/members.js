import { defineStore } from 'pinia'
import { reactive } from 'vue'
import { apiFetch } from '@/lib/api'

// 백엔드 role은 대문자(STUDENT/INSTRUCTOR/ADMIN)라, 기존 소문자 컨벤션(roleLabels 등
// mockAdminMembers.js가 그대로 쓰임)이 안 깨지게 스토어 경계에서 변환한다.
function memberFromApi(m) {
  return {
    id: m.id, name: m.name, email: m.email, role: m.role.toLowerCase(),
    className: m.className, serialNumber: m.serialNumber,
  }
}
function signupFromApi(s) {
  return {
    id: s.id, name: s.name, email: s.email, requestedRole: s.requestedRole.toLowerCase(),
    className: s.className, serialNumber: s.serialNumber,
  }
}

export const useMembersStore = defineStore('members', () => {
  const pending = reactive([])
  const members = reactive([])

  async function loadAll() {
    const [membersRes, pendingRes] = await Promise.all([
      apiFetch('/api/members'),
      apiFetch('/api/members/pending'),
    ])
    members.splice(0, members.length, ...membersRes.map(memberFromApi))
    pending.splice(0, pending.length, ...pendingRes.map(signupFromApi))
  }

  async function requestSignup({ name, email, requestedRole, className, serialNumber, password }) {
    const created = await apiFetch('/api/members/signup', {
      method: 'POST',
      body: { name, email, requestedRole: requestedRole.toUpperCase(), className, serialNumber, password },
    })
    pending.push(signupFromApi(created))
  }

  async function approve(signup) {
    const member = await apiFetch(`/api/members/${signup.id}/approve`, { method: 'POST' })
    const idx = pending.findIndex((p) => p.id === signup.id)
    if (idx !== -1) pending.splice(idx, 1)
    members.unshift(memberFromApi(member))
  }

  async function reject(id) {
    await apiFetch(`/api/members/${id}/reject`, { method: 'DELETE' })
    const idx = pending.findIndex((p) => p.id === id)
    if (idx !== -1) pending.splice(idx, 1)
  }

  async function changeRole(memberId, role) {
    const updated = await apiFetch(`/api/members/${memberId}`, {
      method: 'PATCH',
      body: { role: role.toUpperCase() },
    })
    const member = members.find((m) => m.id === memberId)
    if (member) member.role = updated.role.toLowerCase()
  }

  async function deleteMember(member) {
    await apiFetch(`/api/members/${member.id}`, { method: 'DELETE' })
    const idx = members.findIndex((m) => m.id === member.id)
    if (idx !== -1) members.splice(idx, 1)
  }

  return { pending, members, loadAll, requestSignup, approve, reject, changeRole, deleteMember }
})
